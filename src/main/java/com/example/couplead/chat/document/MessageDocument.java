package com.example.couplead.chat.document;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

import com.example.couplead.chat.domain.MessageType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "messages")
@Setting(settingPath = "/elasticsearch/message-settings.json")
public class MessageDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long messageId;

    @Field(type = FieldType.Long)
    private Long coupleId;

    @Field(type = FieldType.Long)
    private Long senderId;

    @Field(type = FieldType.Keyword)
    private String senderNickname;

    @Field(type = FieldType.Keyword)
    private MessageType type;

    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard"), otherFields = {
            @InnerField(suffix = "nori", type = FieldType.Text, analyzer = "couplead_nori", searchAnalyzer = "couplead_nori")
    })
    private String content;

    @Field(type = FieldType.Date)
    private Instant sentAt;

    public void updateContent(
            String content) {
        this.content = content;
    }
}