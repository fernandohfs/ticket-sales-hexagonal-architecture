package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.models.Customer;
import br.com.fullcycle.hexagonal.infrastructure.models.Event;
import br.com.fullcycle.hexagonal.infrastructure.models.Ticket;
import br.com.fullcycle.hexagonal.infrastructure.models.TicketStatus;
import br.com.fullcycle.hexagonal.infrastructure.services.CustomerService;
import br.com.fullcycle.hexagonal.infrastructure.services.EventService;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

class SubscribeCustomerToEventUseCaseTest {

    @Test
    @DisplayName("Deve comprar um ticket de um evento")
    public void testReserveTicket() {
        // given
        final var expectedTicketsSize = 1;
        final var customerId = TSID.fast().toLong();
        final var eventId = TSID.fast().toLong();

        final var aCustomer = new Customer();
        aCustomer.setId(customerId);
        aCustomer.setCpf("12345678901");
        aCustomer.setEmail("john.doe@gmail.com");
        aCustomer.setName("John Doe");

        final var anEvent = new Event();
        anEvent.setId(eventId);
        anEvent.setName("Disney");
        anEvent.setTotalSpots(10);

        final var subscribeInput =
                new SubscribeCustomerToEventUseCase.Input(eventId, customerId);

        // when
        final var customerService = mock(CustomerService.class);
        final var eventService = mock(EventService.class);

        when(customerService.findById(customerId)).thenReturn(Optional.of(aCustomer));
        when(eventService.findById(eventId)).thenReturn(Optional.of(anEvent));
        when(eventService.findTicketByEventIdAndCustomerId(eventId, customerId)).thenReturn(Optional.empty());
        when(eventService.save(any())).thenAnswer(a -> {
            final var e = a.getArgument(0, Event.class);
            Assertions.assertEquals(expectedTicketsSize, e.getTickets().size());
            return e;
        });

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var output = useCase.execute(subscribeInput);

        // then
        Assertions.assertEquals(eventId, output.eventId());
        Assertions.assertNotNull(output.reservationDate());
        Assertions.assertEquals(TicketStatus.PENDING.name(), output.ticketStatus());
    }

    @Test
    @DisplayName("Não deve comprar um ticket de um cliente que não existe")
    public void testReserveTicketWithoutCustomer() {
        // given
        final var expectedError = "Customer not found";
        final var customerId = TSID.fast().toLong();
        final var eventId = TSID.fast().toLong();

        final var subscribeInput =
                new SubscribeCustomerToEventUseCase.Input(eventId, customerId);

        // when
        final var customerService = mock(CustomerService.class);
        final var eventService = mock(EventService.class);

        when(customerService.findById(customerId)).thenReturn(Optional.empty());

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var actualException =
                Assertions.assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve comprar um ticket de um evento que não existe")
    public void testReserveTicketWithoutEvent() {
        // given
        final var expectedError = "Event not found";
        final var customerId = TSID.fast().toLong();
        final var eventId = TSID.fast().toLong();

        final var aCustomer = new Customer();
        aCustomer.setId(customerId);
        aCustomer.setCpf("12345678901");
        aCustomer.setEmail("john.doe@gmail.com");
        aCustomer.setName("John Doe");

        final var subscribeInput =
                new SubscribeCustomerToEventUseCase.Input(eventId, customerId);

        // when
        final var customerService = mock(CustomerService.class);
        final var eventService = mock(EventService.class);

        when(customerService.findById(customerId)).thenReturn(Optional.of(aCustomer));
        when(eventService.findById(eventId)).thenReturn(Optional.empty());

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var actualException =
                Assertions.assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Um mesmo cliente não pode comprar mais de um ticket por evento")
    public void testReserveTicketMoreThanOnce() {
        // given
        final var expectedError = "Ticket already subscribed";
        final var expectedTicketsSize = 1;
        final var customerId = TSID.fast().toLong();
        final var eventId = TSID.fast().toLong();

        final var aCustomer = new Customer();
        aCustomer.setId(customerId);
        aCustomer.setCpf("12345678901");
        aCustomer.setEmail("john.doe@gmail.com");
        aCustomer.setName("John Doe");

        final var anEvent = new Event();
        anEvent.setId(eventId);
        anEvent.setName("Disney");
        anEvent.setTotalSpots(10);

        final var subscribeInput =
                new SubscribeCustomerToEventUseCase.Input(eventId, customerId);

        // when
        final var customerService = mock(CustomerService.class);
        final var eventService = mock(EventService.class);

        when(customerService.findById(customerId)).thenReturn(Optional.of(aCustomer));
        when(eventService.findById(eventId)).thenReturn(Optional.of(anEvent));
        when(eventService.findTicketByEventIdAndCustomerId(eventId, customerId)).thenReturn(Optional.of(new Ticket()));

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var actualException =
                Assertions.assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Um mesmo cliente não pode comprar de um evento que não há mais cadeiras")
    public void testReserveTicketWithoutSlots() {
        // given
        final var expectedError = "Event sold out";
        final var customerId = TSID.fast().toLong();
        final var eventId = TSID.fast().toLong();

        final var aCustomer = new Customer();
        aCustomer.setId(customerId);
        aCustomer.setCpf("12345678901");
        aCustomer.setEmail("john.doe@gmail.com");
        aCustomer.setName("John Doe");

        final var anEvent = new Event();
        anEvent.setId(eventId);
        anEvent.setName("Disney");
        anEvent.setTotalSpots(0);

        final var subscribeInput =
                new SubscribeCustomerToEventUseCase.Input(eventId, customerId);

        // when
        final var customerService = mock(CustomerService.class);
        final var eventService = mock(EventService.class);

        when(customerService.findById(customerId)).thenReturn(Optional.of(aCustomer));
        when(eventService.findById(eventId)).thenReturn(Optional.of(anEvent));
        when(eventService.findTicketByEventIdAndCustomerId(eventId, customerId)).thenReturn(Optional.empty());

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var actualException =
                Assertions.assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }
}