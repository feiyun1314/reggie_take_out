package com.feiyun;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * TODO
 *
 * @author feiyun
 * @date 2023/3/22 15:25
 * @explain
 */
@Slf4j
public class UploadFileTest {
    @Test
    public void test1(){
        String originalFilename="afjskflsekfe.jpg";
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        log.info("suffix:"+suffix);
    }

}
