package com.example.couplead.chat.document;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "messages")
public class MessageDocument {
    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long coupleId;

    @Field(type = FieldType.Long)
    private Long senderId;

    @Field(type = FieldType.Keyword)
    private String senderNickname;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String content;

    @Field(type = FieldType.Date)
    private LocalDateTime sentAt;
}
