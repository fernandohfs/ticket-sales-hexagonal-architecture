package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.application.UseCase;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.models.Event;
import br.com.fullcycle.hexagonal.services.EventService;
import br.com.fullcycle.hexagonal.services.PartnerService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class CreateEventUseCase
        extends UseCase<CreateEventUseCase.Input, CreateEventUseCase.Output> {

    private final PartnerService partnerService;
    private final EventService eventService;

    public CreateEventUseCase(PartnerService partnerService, EventService eventService) {
        this.partnerService = Objects.requireNonNull(partnerService);
        this.eventService = Objects.requireNonNull(eventService);
    }

    @Override
    public Output execute(final Input input) {
        var event = new Event();
        event.setDate(LocalDate.parse(input.date, DateTimeFormatter.ISO_DATE));
        event.setName(input.name);
        event.setTotalSpots(input.totalSpots);

        partnerService.findById(input.partnerId)
                .ifPresentOrElse(event::setPartner, () -> {
                    throw new ValidationException("Partner not found");
                });

        event = eventService.save(event);

        return new Output(event.getId(), input.date, event.getName(), input.partnerId, input.totalSpots);
    }

    public record Input(String date, String name, Long partnerId, Integer totalSpots) {
    }

    public record Output(Long id, String date, String name, Long partnerId, Integer totalSpots) {
    }
}
