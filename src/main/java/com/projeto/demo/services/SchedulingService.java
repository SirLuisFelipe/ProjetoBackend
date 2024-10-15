package com.projeto.demo.services;

import com.projeto.demo.dto.CreateSchedulingDto;
import com.projeto.demo.entities.Payment;
import com.projeto.demo.entities.Scheduling;
import com.projeto.demo.entities.Track;
import com.projeto.demo.entities.User;
import com.projeto.demo.repositories.SchedulingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SchedulingService {

    @Autowired
    private SchedulingRepository schedulingRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private TrackService trackService;


    public Scheduling createScheduling(CreateSchedulingDto createSchedulingDto) {
        Scheduling scheduling = mapToEntity(createSchedulingDto);

        return schedulingRepository.save(scheduling);
    }

    public Scheduling updateScheduling(CreateSchedulingDto createSchedulingDto){
        Scheduling scheduling = mapToEntity(createSchedulingDto);

        return schedulingRepository.save(scheduling);
    }

    public Scheduling mapToEntity(CreateSchedulingDto createSchedulingDto){
        Scheduling scheduling = new Scheduling();

        if (createSchedulingDto.getId() != null) {
            scheduling = findSchedulingById(createSchedulingDto.getId());
        }

        User user = userService.findById(createSchedulingDto.getUserId());
        Payment payment = paymentService.findPaymentById(createSchedulingDto.getPaymentId());
        Track track = trackService.findTrackById(createSchedulingDto.getTrackId());

        scheduling.setUser(user);
        scheduling.setPayment(payment);
        scheduling.setTrack(track);
        scheduling.setScheduledTimeEnd(createSchedulingDto.getScheduledTimeEnd());
        scheduling.setScheduledTimeStart(createSchedulingDto.getScheduledTimeStart());
        scheduling.setPaymentValue(createSchedulingDto.getPaymentValue());

        return scheduling;
    }

    public List<Scheduling> listSchedulings(){
        return schedulingRepository.findAll();
    }

    public Scheduling findSchedulingById(Long id){
        return schedulingRepository.findById(id).orElseThrow(() -> new RuntimeException("Scheduling not found"));
    }

    public void deleteScheduling(Long id){
        Scheduling scheduling = findSchedulingById(id);
        schedulingRepository.delete(scheduling);
    }

    public List<Scheduling> listSchedulingsByUserId(Long userId) {
        return schedulingRepository.findByUserId(userId);
    }

    public List<Scheduling> listSchedulingsByTrackId(Long trackId){
        return schedulingRepository.findByTrackId(trackId);
    }
}
