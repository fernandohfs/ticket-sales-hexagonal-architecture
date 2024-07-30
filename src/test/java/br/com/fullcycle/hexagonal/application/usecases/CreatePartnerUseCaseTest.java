package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.models.Partner;
import br.com.fullcycle.hexagonal.services.PartnerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CreatePartnerUseCaseTest {

    @Test
    @DisplayName("Deve criar um parceiro")
    public void testCreate() {
        // given
        final var expectedCnpj = "41536538000100";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        final var createInput = new CreatePartnerUseCase.Input(expectedCnpj, expectedEmail, expectedName);

        // when
        final var partnerService = Mockito.mock(PartnerService.class);

        when(partnerService.findByCnpj(expectedCnpj)).thenReturn(Optional.empty());
        when(partnerService.findByEmail(expectedEmail)).thenReturn(Optional.empty());
        when(partnerService.save(any())).thenAnswer(a -> {
           var partner = a.getArgument(0, Partner.class);
           partner.setId(UUID.randomUUID().getMostSignificantBits());
           return partner;
        });

        final var useCase = new CreatePartnerUseCase(partnerService);
        final var output = useCase.execute(createInput);

        // then
        Assertions.assertNotNull(output.id());
        Assertions.assertEquals(expectedCnpj, output.cnpj());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedName, output.name());
    }
}