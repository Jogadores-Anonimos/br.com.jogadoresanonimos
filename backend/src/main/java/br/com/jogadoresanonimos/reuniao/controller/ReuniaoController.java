package br.com.jogadoresanonimos.reuniao.controller;

import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.reuniao.dto.ReuniaoResponse;
import br.com.jogadoresanonimos.reuniao.service.ReuniaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reunioes")
public class ReuniaoController {

    private final ReuniaoService reuniaoService;

    public ReuniaoController(ReuniaoService reuniaoService) {
        this.reuniaoService = reuniaoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReuniaoResponse>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.ok(reuniaoService.listarTodas()));
    }
}
