package com.okava.pay.services;

import com.okava.pay.models.Event;
import com.okava.pay.models.enums.EEventStatus;
import com.okava.pay.utils.dtos.CreateOrUpdateEventDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface IEventService {

    List<Event> all();

    Event findById(Long id);

    List<Event> createdByMe();

    List<Event> createdBy(Long userId);

    List<Event> search(String title);

    List<Event> between(LocalDateTime start, LocalDateTime end);

    Event create(CreateOrUpdateEventDTO dto);

    Event update(Long id, CreateOrUpdateEventDTO dto);

    Event changeStatus(Long id, EEventStatus newStatus);

    Page<Event> all(Pageable pageable);

    List<Event> byStatus(EEventStatus status);

    Page<Event> byStatus(EEventStatus status, Pageable pageable);
}
