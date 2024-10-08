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

    // post http://localhost:8080/user (JSON com os dados)
    public User create(final User user){
        Assert.isTrue(this.getByCpf(user.getCpf()).isEmpty(),"Já existe um usuário cadastrado com o CPF informado.");
        return repository.save(user);
    }

    // put http://localhost:8080/user (JSON com os dados)
    public User update(final User user){
        Assert.notNull(user.getId(),"ID deve ser informado");
        Assert.isTrue(repository.findById(user.getId()).isPresent(),"Usuário não encontrado");
        return repository.save(user);
    }
    // get http://localhost:8080/user/{CPF}
    public Optional<User> getByCpf(final String cpf){
        return repository.findByCpf(cpf);
    }

    // get http://localhost:8080/user
    public List<User> list() {
        return repository.findAll();
    }

    // get http://localhost:8080/user/0/2
    public Page<User> listPage(final Integer page, final Integer size) {
        return repository.findAll(PageRequest.of(page, size));
    }
    public void deleteByCpf(String cpf){
        Optional<User> user = repository.findByCpf(cpf);
        if(user.isPresent()) {
            repository.delete(user.get());
        } else {
            throw new RuntimeException("Usuário não encontrado com o CPF:" + cpf);
        }
    }
    @Autowired
    private UserRepository userRepository;
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
}