package br.com.fullcycle.hexagonal.application;

public abstract class UnitUseCase<INPUT> {

    // Esse recebe um parâmetro, executa algo e não retorna um Output.

    public abstract void execute(INPUT input);
}
