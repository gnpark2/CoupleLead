// package com.example.couplead.event.producer;

// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.stereotype.Component;

// import com.example.couplead.event.dto.CoupleAnniversaryUpdatedEvent;

// import lombok.RequiredArgsConstructor;

// @Component
// @RequiredArgsConstructor
// public class CoupleEventProducer {
//     private final KafkaTemplate<String, CoupleAnniversaryUpdatedEvent> kafkaTemplate;
    
//     public void publishAnniversaryUpdated(CoupleAnniversaryUpdatedEvent event) {
//         kafkaTemplate.send("couple-anniversary-updated", event.coupleId().toString(), event);
//     }
// }
