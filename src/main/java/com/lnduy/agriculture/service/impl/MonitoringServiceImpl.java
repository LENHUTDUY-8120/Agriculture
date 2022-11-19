package com.lnduy.agriculture.service.impl;

import com.lnduy.agriculture.domain.Monitoring;
import com.lnduy.agriculture.repository.MonitoringRepository;
import com.lnduy.agriculture.service.MonitoringService;
import com.lnduy.agriculture.service.dto.MonitoringDTO;
import com.lnduy.agriculture.service.mapper.MonitoringMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Monitoring}.
 */
@Service
@Transactional
public class MonitoringServiceImpl implements MonitoringService {

    private final Logger log = LoggerFactory.getLogger(MonitoringServiceImpl.class);

    private final MonitoringRepository monitoringRepository;

    private final MonitoringMapper monitoringMapper;

    public MonitoringServiceImpl(MonitoringRepository monitoringRepository, MonitoringMapper monitoringMapper) {
        this.monitoringRepository = monitoringRepository;
        this.monitoringMapper = monitoringMapper;
    }

    @Override
    public MonitoringDTO save(MonitoringDTO monitoringDTO) {
        log.debug("Request to save Monitoring : {}", monitoringDTO);
        Monitoring monitoring = monitoringMapper.toEntity(monitoringDTO);
        monitoring = monitoringRepository.save(monitoring);
        return monitoringMapper.toDto(monitoring);
    }

    @Override
    public MonitoringDTO update(MonitoringDTO monitoringDTO) {
        log.debug("Request to update Monitoring : {}", monitoringDTO);
        Monitoring monitoring = monitoringMapper.toEntity(monitoringDTO);
        monitoring = monitoringRepository.save(monitoring);
        return monitoringMapper.toDto(monitoring);
    }

    @Override
    public Optional<MonitoringDTO> partialUpdate(MonitoringDTO monitoringDTO) {
        log.debug("Request to partially update Monitoring : {}", monitoringDTO);

        return monitoringRepository
            .findById(monitoringDTO.getId())
            .map(existingMonitoring -> {
                monitoringMapper.partialUpdate(existingMonitoring, monitoringDTO);

                return existingMonitoring;
            })
            .map(monitoringRepository::save)
            .map(monitoringMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonitoringDTO> findAll() {
        log.debug("Request to get all Monitorings");
        return monitoringRepository.findAll().stream().map(monitoringMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MonitoringDTO> findOne(Long id) {
        log.debug("Request to get Monitoring : {}", id);
        return monitoringRepository.findById(id).map(monitoringMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Monitoring : {}", id);
        monitoringRepository.deleteById(id);
    }
}
