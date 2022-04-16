package jp.co.tk.domain.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.CompletableFuture;

@SpringJUnitConfig(jp.co.tk.domain.service.YAServiceTest.Config.class)
public class CsvCreatorServiceTest {

    @Autowired
    private CsvCreatorService csvServ;

    @Autowired
    private YAService yaServ;

    @ComponentScan("jp.co.tk.domain")
    static class Config {
    }

    @Test
    public void CSVが生成出来るか() throws Exception {
        var seller = this.yaServ.findSellerBySellerName("tomomooo0716", 5, 0);
        var result = csvServ.create(seller);
        CompletableFuture.completedFuture(result);
    }


}
