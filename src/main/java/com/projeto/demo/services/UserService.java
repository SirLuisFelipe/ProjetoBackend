package com.projeto.demo.services;

import com.projeto.demo.models.User;
import com.projeto.demo.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    public UserService(final UserRepository repository){
        this.repository = repository;
    }
    private final UserRepository repository;

    public User create(final User user){
        Assert.isTrue(this.getByCpf(user.getCpf()).isEmpty(),"Já existe um usuário cadastrado com o CPF informado.");
        return repository.save(user);
    }

    public User update(final User user){
        Assert.notNull(user.getIdUser(),"ID deve ser informado");
        Assert.isTrue(repository.findById(user.getIdUser()).isPresent(),"Usuário não encontrado"); //Realizar verificacao
        return repository.save(user);
    }
    public Optional<User> getByCpf(final String cpf){
        return repository.findByCpf(cpf);
    }

    public List<User> list() {
        return repository.findAll();
    }

    public Page<User> listPage(final Integer page, final Integer size) {
        return repository.findAll(PageRequest.of(page, size));
    }



}