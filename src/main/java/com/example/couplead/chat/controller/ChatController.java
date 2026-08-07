// package com.example.couplead.chat.controller;

// import java.security.Principal;
// import java.time.LocalDateTime;
// import java.util.List;

// import org.springframework.messaging.handler.annotation.MessageMapping;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.example.couplead.auth.security.CustomUserDetails;
// import com.example.couplead.chat.dto.request.ChatMessageRequest;
// import com.example.couplead.chat.dto.response.ChatHistoryResponse;
// import com.example.couplead.chat.dto.response.ChatMessageResponse;
// import com.example.couplead.chat.service.ChatService;
// import com.example.couplead.common.response.ApiResponse;
// import com.example.couplead.event.producer.ChatEventProducer;
// import com.example.couplead.user.domain.User;
// import com.example.couplead.user.repository.UserRepository;

// import lombok.RequiredArgsConstructor;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;


// @RestController
// @RequiredArgsConstructor
// @RequestMapping("/api/chat")
// public class ChatController {
//     private final ChatEventProducer chatEventProducer;
//     private final UserRepository userRepository;
//     private final ChatService chatService;

//     @MessageMapping("/chat/send")
//     public void send(
//             ChatMessageRequest request,
//             Principal principal
//     ) {

//         if (principal == null) {
//             throw new IllegalStateException("Principal is null");
//         }

//         Long userId =
//                 Long.parseLong(principal.getName());

//         User user = userRepository.findById(userId)
//                 .orElseThrow();

//         ChatMessageResponse message =
//                 new ChatMessageResponse(
//                         request.coupleId(),
//                         user.getId(),
//                         user.getNickname(),
//                         request.content(),
//                         LocalDateTime.now()
//                 );

//         chatEventProducer.publish(message);
//     }

//     @GetMapping("/{coupleId}")
//     public ApiResponse<List<ChatHistoryResponse>> getMessages(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long coupleId) {
//         return ApiResponse.success(
//             chatService.getMessages(
//                 userDetails.getUser().getId(),
//                 coupleId
//             )
//         );
//     }
    
// }
