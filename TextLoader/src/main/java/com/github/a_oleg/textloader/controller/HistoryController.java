package com.github.a_oleg.textloader.controller;

import com.github.a_oleg.textloader.models.Request;
import com.github.a_oleg.textloader.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.util.ArrayList;

@Controller
public class HistoryController {
    private final HistoryService historyService;

    @Autowired
    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @RequestMapping(value = "/downloadHistory", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)

    public FileSystemResource downloadHistory(Model model) {
        ArrayList<Request> requests = historyService.createRequestsCollection();
        File file = historyService.createFileForData(requests);
        return new FileSystemResource(file);
    }
}
