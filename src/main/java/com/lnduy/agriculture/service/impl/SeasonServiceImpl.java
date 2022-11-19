package com.lnduy.agriculture.service.impl;

import com.lnduy.agriculture.domain.Season;
import com.lnduy.agriculture.repository.SeasonRepository;
import com.lnduy.agriculture.service.SeasonService;
import com.lnduy.agriculture.service.dto.SeasonDTO;
import com.lnduy.agriculture.service.mapper.SeasonMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Season}.
 */
@Service
@Transactional
public class SeasonServiceImpl implements SeasonService {

    private final Logger log = LoggerFactory.getLogger(SeasonServiceImpl.class);

    private final SeasonRepository seasonRepository;

    private final SeasonMapper seasonMapper;

    public SeasonServiceImpl(SeasonRepository seasonRepository, SeasonMapper seasonMapper) {
        this.seasonRepository = seasonRepository;
        this.seasonMapper = seasonMapper;
    }

    @Override
    public SeasonDTO save(SeasonDTO seasonDTO) {
        log.debug("Request to save Season : {}", seasonDTO);
        Season season = seasonMapper.toEntity(seasonDTO);
        season = seasonRepository.save(season);
        return seasonMapper.toDto(season);
    }

    @Override
    public SeasonDTO update(SeasonDTO seasonDTO) {
        log.debug("Request to update Season : {}", seasonDTO);
        Season season = seasonMapper.toEntity(seasonDTO);
        season = seasonRepository.save(season);
        return seasonMapper.toDto(season);
    }

    @Override
    public Optional<SeasonDTO> partialUpdate(SeasonDTO seasonDTO) {
        log.debug("Request to partially update Season : {}", seasonDTO);

        return seasonRepository
            .findById(seasonDTO.getId())
            .map(existingSeason -> {
                seasonMapper.partialUpdate(existingSeason, seasonDTO);

                return existingSeason;
            })
            .map(seasonRepository::save)
            .map(seasonMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SeasonDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Seasons");
        return seasonRepository.findAll(pageable).map(seasonMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SeasonDTO> findOne(Long id) {
        log.debug("Request to get Season : {}", id);
        return seasonRepository.findById(id).map(seasonMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Season : {}", id);
        seasonRepository.deleteById(id);
    }
}
