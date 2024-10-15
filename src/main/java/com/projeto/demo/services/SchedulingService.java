package com.projeto.demo.services;

import com.projeto.demo.entities.Scheduling;
import com.projeto.demo.repositories.SchedulingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SchedulingService {
    @Autowired
    private SchedulingRepository schedulingRepository;
    //  Buscar todos agendamentos
    public List<Scheduling> getAllScheduling(){
        return schedulingRepository.findAll();
    }
    //  Buscar agendamento pelo Id
    public Optional<Scheduling> getSchedulingById(Long id){

        return schedulingRepository.findById(id);
    }
    //  Cria novo agendamento
    public Scheduling createScheduling(Scheduling scheduling){

        return schedulingRepository.save(scheduling);
    }
    //  Atualiza agendamento
    public Scheduling updateScheduling(Scheduling scheduling){

        return schedulingRepository.save(scheduling);
    }
    //    Exclui um agendamento
    public void deleteScheduling(Long id){

        schedulingRepository.deleteById(id);
    }

    //  Busca todos agendamentos de um usuario
    public List<Scheduling> getSchedulingsByUserId(Long userId) {
        return schedulingRepository.findByUserId(userId);
    }
    //  Buscar todos agendamentos por tipo de pista
    public List<Scheduling> getSchedulingsByTrackid(Integer trackid){
        return schedulingRepository.findByTrackId(trackid);
    }
}
