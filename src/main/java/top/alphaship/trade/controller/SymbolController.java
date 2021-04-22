package top.alphaship.trade.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import top.alphaship.trade.data.SymbolData;

import java.util.*;

@RestController
@RequestMapping("/symbol")
public class SymbolController {

    @RequestMapping("/setContractSymbol")
    @ResponseBody
    public Set<String> setContractSymbol(String symbol) {
        String[] list = symbol.split(",");
        SymbolData.contractSymbols.addAll(Arrays.asList(list));
        return SymbolData.contractSymbols;
    }

    @RequestMapping("/setSpotSymbol")
    @ResponseBody
    public Set<String> setSpotSymbol(String symbol) {
        String[] list = symbol.split(",");
        SymbolData.spotSymbols.addAll(Arrays.asList(list));
        return SymbolData.spotSymbols;
    }

    @RequestMapping("/getAllSymbol")
    @ResponseBody
    public Map<String, Set<String>> getAllSymbol() {
        Map<String, Set<String>> result = new HashMap<>();
        result.put("contractSymbols", SymbolData.contractSymbols);
        result.put("spotSymbols", SymbolData.spotSymbols);
        return result;
    }

    @RequestMapping("/clearAllSymbol")
    @ResponseBody
    public String clearAllSymbol() {
        SymbolData.contractSymbols.clear();
        SymbolData.spotSymbols.clear();
        return "success";
    }
}
