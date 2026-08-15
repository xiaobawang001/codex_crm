package com.example.crm.modules.dict.interfaces.rest;

import com.example.crm.modules.dict.application.DictAppService;
import com.example.crm.modules.dict.domain.model.SysDictItem;
import com.example.crm.shared.api.Result;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dict")
public class DictController {

    private final DictAppService dictAppService;

    public DictController(DictAppService dictAppService) {
        this.dictAppService = dictAppService;
    }

    @GetMapping("/{dictType}")
    public Result<List<SysDictItem>> listByType(@PathVariable String dictType) {
        return Result.ok(dictAppService.listByType(dictType));
    }

    @GetMapping("/all/list")
    public Result<List<SysDictItem>> listAll(@RequestParam(required = false) String dictType,
                                             @RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) Integer status) {
        return Result.ok(dictAppService.listAll(dictType, keyword, status));
    }

    @PostMapping
    public Result<Void> save(@RequestBody SysDictItem item) {
        dictAppService.save(item);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dictAppService.delete(id);
        return Result.ok();
    }
}
