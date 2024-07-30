package br.com.fullcycle.hexagonal.application;

public abstract class UseCase<INPUT, OUTPUT> {

    // 1. Cada caso de uso tem um Input e um Output próprio. Não retorna a entidade, o agregado, ou objeto de valor.
    // 2. O caso de uso implementa o padrão Command (um método chamado execute que recebe um parâmetro e retorna uma coisa).

    public abstract OUTPUT execute(INPUT input);
}
