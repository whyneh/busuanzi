package com.yww.busuanzi.util;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 * <p>
 *      雪花ID生成器
 *      <a href="https://github.com/yitter/IdGenerator">github</a>
 * </p>
 *
 * @author yww
 * @since 2025/8/4
 */
@Component
public class IdGenerator  {

    @PostConstruct
    public void idGeneratorRegister() {
        // 创建 IdGeneratorOptions 对象，可在构造函数中输入 WorkerId：
        // 当前已使用的workId，前置机是1, 2是内网
        IdGeneratorOptions options = new IdGeneratorOptions((short) 1);
        // 机器码位长,默认值6，限定 WorkerId 最大值为2^6-1，即默认最多支持64个节点。
        options.WorkerIdBitLength = 10;
        // 默认值6，限制每毫秒生成的ID个数。若生成速度超过5万个/秒，建议加大 SeqBitLength 到 10。
        options.SeqBitLength = 6;

        // 保存参数（务必调用，否则参数设置不生效）：
        YitIdHelper.setIdGenerator(options);
    }

    public static void main(String[] args) {
        System.out.println(YitIdHelper.nextId());
    }

}

