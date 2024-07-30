package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.models.Partner;
import br.com.fullcycle.hexagonal.services.PartnerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

class GetPartnerByIdUseCaseTest {

    @Test
    @DisplayName("Deve obter um parceiro por id")
    public void testGetById() {
        // given
        final var expectedId = UUID.randomUUID().getMostSignificantBits();
        final var expectedCnpj = "41536538000100";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        final var aPartner = new Partner();
        aPartner.setId(expectedId);
        aPartner.setCnpj(expectedCnpj);
        aPartner.setEmail(expectedEmail);
        aPartner.setName(expectedName);

        final var input = new GetPartnerByIdUseCase.Input(expectedId);

        // when
        final var partnerService = Mockito.mock(PartnerService.class);
        when(partnerService.findById(expectedId)).thenReturn(Optional.of(aPartner));

        final var useCase = new GetPartnerByIdUseCase(partnerService);
        final var output = useCase.execute(input).get();

        // then
        Assertions.assertEquals(expectedId, output.id());
        Assertions.assertEquals(expectedCnpj, output.cnpj());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedName, output.name());
    }

    @Test
    @DisplayName("Deve obter vazio ao tentar recuperar um parceiro não existente por id")
    public void testGetByIdWithInvalidId() {
        // given
        final var expectedId = UUID.randomUUID().getMostSignificantBits();

        final var input = new GetPartnerByIdUseCase.Input(expectedId);

        // when
        final var partnerService = Mockito.mock(PartnerService.class);
        when(partnerService.findById(expectedId)).thenReturn(Optional.empty());

        final var useCase = new GetPartnerByIdUseCase(partnerService);
        final var output = useCase.execute(input);

        // then
        Assertions.assertTrue(output.isEmpty());
    }
}