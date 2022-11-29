package com.lnduy.agriculture.schedule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lnduy.agriculture.domain.Monitoring;
import com.lnduy.agriculture.service.*;
import com.lnduy.agriculture.service.criteria.DeviceCriteria;
import com.lnduy.agriculture.service.criteria.FieldCriteria;
import com.lnduy.agriculture.service.criteria.TaskCriteria;
import com.lnduy.agriculture.service.dto.*;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tech.jhipster.service.filter.LongFilter;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ScheduleTask {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleTask.class);
    private int qos = 2;
    private String broker = "tcp://127.0.0.1:1883";
    private String clientId = "SmartAgricultureBE8120";
    private MemoryPersistence persistence = new MemoryPersistence();
    private final EventService eventService;
    private final TaskService taskService;
    private final MonitoringService monitoringService;
    private final TaskQueryService taskQueryService;
    private final FieldQueryService fieldQueryService;
    private final DeviceQueryService deviceQueryService;
    private final EventCategoryService eventCategoryService;
    private MqttClient client;

    private final MailService mailService;

    public ScheduleTask(MailService mailService, EventService eventService, FieldService fieldService, TaskService taskService,
                      TaskQueryService taskQueryService, FieldQueryService fieldQueryService, MonitoringService monitoringService,
                        DeviceQueryService deviceQueryService, EventCategoryService eventCategoryService) {
        this.mailService = mailService;
        this.eventService = eventService;
        this.taskService = taskService;
        this.taskQueryService = taskQueryService;
        this.fieldQueryService = fieldQueryService;
        this.monitoringService = monitoringService;
        this.deviceQueryService = deviceQueryService;
        this.eventCategoryService = eventCategoryService;
        try {
            this.client = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + broker);
            client.connect(connOpts);
            System.out.println("Connected");

            client.subscribe("event", 2, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                    ObjectMapper mapper = new ObjectMapper();
                    String msg = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8);
                    TypeReference<HashMap<String, Object>> typeRef
                        = new TypeReference<HashMap<String, Object>>() {
                    };
                    Map<String, Object> map = mapper.readValue(msg, typeRef);
                    EventDTO eventDTO = new EventDTO();
                    EventCategoryDTO eventCategoryDTO = eventCategoryService.findOne(1l).get();
                    eventDTO.setCategory(eventCategoryDTO);
                    eventDTO.setContent((String) map.get("content"));
                    eventDTO.setStartAt(LocalDate.parse((CharSequence) map.get("date")));
                    eventDTO.setDescriptions((String) map.get("time"));
                    eventService.save(eventDTO);
                }
            });

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    logger.error("MQTT disconnected !!!");
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) {
                    System.out.println(s);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("except " + me);
            me.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 */2 ? * *")
    public void saveSensorDataPerHour() throws MqttException {
        client.subscribe("sensor", 2, new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                ObjectMapper mapper = new ObjectMapper();
                String msg = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8);
                TypeReference<HashMap<String, Object>> typeRef
                    = new TypeReference<HashMap<String, Object>>() {
                };
                Map<String, Object> map = mapper.readValue(msg, typeRef);

                FieldCriteria fieldCriteria = new FieldCriteria();
                List<FieldDTO> fields = fieldQueryService.findByCriteria(fieldCriteria);
                fields.forEach(field -> {
                    DeviceCriteria deviceCriteria = new DeviceCriteria();
                    LongFilter longFilter = new LongFilter();
                    longFilter.setEquals(field.getId());
                    deviceCriteria.setFieldId(longFilter);
                    List<DeviceDTO> devices = deviceQueryService.findByCriteria(deviceCriteria);
                    devices.forEach(deviceDTO -> {
                        if (deviceDTO.getCategory().getId() != 1){
                            HashMap<String, Object> hmap = (HashMap<String, Object>) map.get(deviceDTO.getCode());
                            MonitoringDTO monitoringDTO = new MonitoringDTO();
                            monitoringDTO.setDevice(deviceDTO);
                            monitoringDTO.setField(deviceDTO.getField());
                            monitoringDTO.setValue((Double) hmap.get("value"));
                            monitoringDTO.setCreatedAt(LocalDate.parse((CharSequence) map.get("date")));
                            monitoringService.save(monitoringDTO);
                        }
                    });
                });
                client.unsubscribe("sensor");
            }
        });
    }

    @Scheduled(cron = "0 0 6 * * ?")
    public void reminderTask() {
        TaskCriteria taskCriteria = new TaskCriteria();
        List<TaskDTO> tasks = taskQueryService.findByCriteria(taskCriteria);
        tasks.forEach(task -> {
            if (task.getStartDate().isEqual(LocalDate.now())){
                task.getEmployees().forEach(empl -> {
                    if (empl.getEmail() != null || empl.getEmail() != "") {
                        mailService.sendEmailTask(empl.getEmail(), task);
                    }
                });
            }
        });
    }
}
