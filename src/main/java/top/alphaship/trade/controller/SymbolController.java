package top.alphaship.trade.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import top.alphaship.trade.data.SymbolData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/symbol")
public class SymbolController {

    @RequestMapping("/setContractSymbol")
    @ResponseBody
    public Set<String> setContractSymbol(String symbol) {
        String[] list = symbol.split(",");
        for (int i = 0; i < list.length; i++) {
            SymbolData.contractSymbols.add(symbol);
        }
        return SymbolData.contractSymbols;
    }

    @RequestMapping("/setSpotSymbol")
    @ResponseBody
    public Set<String> setSpotSymbol(String symbol) {
        String[] list = symbol.split(",");
        for (int i = 0; i < list.length; i++) {
            SymbolData.spotSymbols.add(symbol);
        }
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
}
