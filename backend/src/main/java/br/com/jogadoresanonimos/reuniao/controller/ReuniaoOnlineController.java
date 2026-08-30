package br.com.jogadoresanonimos.reuniao.controller;

import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoOnlineResponse;
import br.com.jogadoresanonimos.reuniao.service.ReuniaoOnlineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reunioes-online")
public class ReuniaoOnlineController {

    private final ReuniaoOnlineService reuniaoOnlineService;

    public ReuniaoOnlineController(ReuniaoOnlineService reuniaoOnlineService) {
        this.reuniaoOnlineService = reuniaoOnlineService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReuniaoOnlineResponse>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.ok(reuniaoOnlineService.listarTodas()));
    }
}
