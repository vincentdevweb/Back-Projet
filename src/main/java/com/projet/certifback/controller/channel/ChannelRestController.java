package com.projet.certifback.controller.channel;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projet.certifback.controller.channel.dto.ChannelDTO;
import com.projet.certifback.controller.channel.dto.ChannelMapper;
import com.projet.certifback.controller.channel.dto.ChannelPostDTO;
import com.projet.certifback.dao.entity.Channel;
import com.projet.certifback.service.ChatService;

@RestController
@RequestMapping("/api/channels")
public class ChannelRestController {
    @Autowired
    private ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChannelDTO>> getAllChannels() {
        List<Channel> entities = chatService.getAllChannels();
        List<ChannelDTO> dtos = new ArrayList<>();
        for (Channel entity : entities)
            dtos.add(ChannelMapper.INSTANCE.convertChannelToDTO(entity));

        return ResponseEntity.ok().body(dtos);
    }

    @GetMapping("{channelId}")
    public ResponseEntity<?> getChannelById(@PathVariable("channelId") Long id) {
        Channel entity = chatService.getChannelById(id);
        if (entity == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("{\"message\": \"Channel not found\"}");
        }
        ChannelDTO dto = ChannelMapper.INSTANCE.convertChannelToDTO(entity);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<String> createChannel(@RequestBody ChannelPostDTO ChannelPostDTO) {
        Channel newChannel = ChannelMapper.INSTANCE.convertDTOToChannel(ChannelPostDTO);
        try {
            Channel newChannelTested = chatService.saveChannel(newChannel);
            if (newChannelTested != null) {
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body("{\"message\": \"Channel created successfully\"}");
            }
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("{\"message\": \"Channel name should be different from 'General' \"}");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("{\"message\": \"Error: JSON\"}");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @PutMapping("{channelId}")
    public ResponseEntity<String> putChannel(@RequestBody ChannelPostDTO channelPutDTO,
            @PathVariable("channelId") Long channelId) {
        Channel channel = ChannelMapper.INSTANCE.convertDTOToChannel(channelPutDTO);
        Channel existingChanel = chatService.updateChannel(channel, channelId);
        if (existingChanel != null) {
            try {
                chatService.saveChannel(existingChanel);
                return ResponseEntity
                        .ok("{\"message\": \"Chanel Put successfully\"}");
            } catch (DataIntegrityViolationException e) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("{\"message\": \"Error: JSON or Channel name should be different from 'General' \"}");
            } catch (Exception e) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("{\"message\": \"Error: " + e.getMessage() + "\"}");
            }
        }
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("{\"message\": \"Chanel Not Found\"}");
    }

    @PatchMapping("{channelId}")
    public ResponseEntity<String> patchChannel(@PathVariable("channelId") Long id,
            @RequestBody ChannelPostDTO ChannelPatchDTO) {
        Channel Channel = ChannelMapper.INSTANCE.convertDTOToChannel(ChannelPatchDTO);
        Channel existingChannel = chatService.patchChannel(Channel, id);
        if (existingChannel == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("{\"message\": \"Chanel Not Found\"}");
        }
        try {
            chatService.saveChannel(existingChannel);
            return ResponseEntity
                    .ok("{\"message\": \"Chanel Patch successfully\"}");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("{channelId}")
    public ResponseEntity<String> deleteChannel(@PathVariable("channelId") Long channelId) {
        if (chatService.deleteChannel(channelId)) {
            return ResponseEntity
                    .status(204)
                    .body("{\"message\": \"Chanel Deleted successfully\"}");
        } else
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("{\"message\": \"Chanel Not Found\"}");
    }
}
