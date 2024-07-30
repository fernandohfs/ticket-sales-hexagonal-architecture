package br.com.fullcycle.hexagonal.controllers;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.usecases.CreatePartnerUseCase;
import br.com.fullcycle.hexagonal.dtos.PartnerDTO;
import br.com.fullcycle.hexagonal.models.Partner;
import br.com.fullcycle.hexagonal.services.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(value = "partners")
public class PartnerController {

    @Autowired
    private PartnerService partnerService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PartnerDTO dto) {
        try {
            final var useCase = new CreatePartnerUseCase(partnerService);
            final var output = useCase.execute(new CreatePartnerUseCase.Input(
                    dto.getCnpj(),
                    dto.getEmail(),
                    dto.getName()));

            return ResponseEntity.created(URI.create("/partners/" + output.id())).body(output);
        } catch (ValidationException ex) {
            return ResponseEntity.unprocessableEntity().body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        var partner = partnerService.findById(id);
        if (partner.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(partner.get());
    }

}
