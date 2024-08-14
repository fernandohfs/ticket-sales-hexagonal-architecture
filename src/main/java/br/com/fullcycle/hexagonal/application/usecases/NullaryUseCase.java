package br.com.fullcycle.hexagonal.application.usecases;

public abstract class NullaryUseCase<OUTPUT> {

    // Esse não recebe um parâmetro, mas executa algo e retorna um Output.

    public abstract OUTPUT execute();
}
