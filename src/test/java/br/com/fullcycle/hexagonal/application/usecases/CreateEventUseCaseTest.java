package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.models.Event;
import br.com.fullcycle.hexagonal.models.Partner;
import br.com.fullcycle.hexagonal.services.EventService;
import br.com.fullcycle.hexagonal.services.PartnerService;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class CreateEventUseCaseTest {

    @Test
    @DisplayName("Deve criar um evento")
    public void testCreate() {
        // given
        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney on Ice";
        final var expectedPartnerId = TSID.fast().toLong();
        final var expectedTotalSpots = 10;

        final var createInput =
                new CreateEventUseCase.Input(expectedDate, expectedName, expectedPartnerId, expectedTotalSpots);

        // when
        final var partnerService = Mockito.mock(PartnerService.class);
        final var eventService = Mockito.mock(EventService.class);

        when(eventService.save(any())).thenAnswer(a -> {
            final var e = a.getArgument(0, Event.class);
            e.setId(TSID.fast().toLong());
            return e;
        });

        when(partnerService.findById(eq(expectedPartnerId)))
                .thenReturn(Optional.of(new Partner()));

        final var useCase = new CreateEventUseCase(partnerService, eventService);
        final var output = useCase.execute(createInput);

        // then
        Assertions.assertEquals(expectedDate, output.date());
        Assertions.assertEquals(expectedName, output.name());
        Assertions.assertEquals(expectedTotalSpots, output.totalSpots());
        Assertions.assertEquals(expectedPartnerId, output.partnerId());
    }

    @Test
    @DisplayName("Não deve criar um evento quando o Partner não for encontrado")
    public void testCreateEvent_whenPartnerDoesntExists_ShouldThrow() {
        // given
        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney on Ice";
        final var expectedPartnerId = TSID.fast().toLong();
        final var expectedTotalSpots = 10;
        final var expectedError = "Partner not found";

        final var createInput =
                new CreateEventUseCase.Input(expectedDate, expectedName, expectedPartnerId, expectedTotalSpots);

        // when
        final var partnerService = Mockito.mock(PartnerService.class);
        final var eventService = Mockito.mock(EventService.class);

        when(partnerService.findById(eq(expectedPartnerId)))
                .thenReturn(Optional.empty());

        final var useCase = new CreateEventUseCase(partnerService, eventService);
        final var actualException =
                Assertions.assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }
}