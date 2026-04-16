package com.zhituan.backend.config;

import com.zhituan.backend.domain.model.forum.ForumPost;
import com.zhituan.backend.repository.forum.ForumPostRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ForumSeedDataInitializer implements CommandLineRunner {

    private final ForumPostRepository forumPostRepository;

    public ForumSeedDataInitializer(ForumPostRepository forumPostRepository) {
        this.forumPostRepository = forumPostRepository;
    }

    @Override
    public void run(String... args) {
        if (forumPostRepository.count() >= 18) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<ForumPost> seeds = List.of(
            createPost("求职新人小林", "面试前先交体检押金，结果公司直接失联", "我在某招聘群看到一个客服岗，面试后HR说流程都过了，但要先交380元体检押金。转账后对方一直拖，最后把我拉黑。大家遇到先交钱的岗位一定要警惕，正规公司不会在入职前收费。", now.minusDays(15)),
            createPost("法务求职者", "高薪无责背后是培训贷，签字前一定看清合同", "对方说月薪8k起步，结果入职前让签培训协议，还引导我办理分期贷款。合同里写着离职需赔付培训费，典型培训贷陷阱。", now.minusDays(14)),
            createPost("应届生阿哲", "先上岗后补合同到底靠不靠谱？", "我上周去一家销售公司试岗，三天后还不给劳动合同，只说先干着。想问大家，这种情况是不是存在用工风险？", now.minusDays(13)),
            createPost("社招打工人", "收到offer后被要求下载不明远程软件，差点中招", "对方自称人事，让我安装远程会议插件进行线上入职，结果软件请求读取短信和通讯录权限。我当时就感觉不对，立刻退出了。", now.minusDays(12)),
            createPost("求职防骗志愿者", "求职黑话：弹性工作很多时候等于默认加班", "最近整理了一些黑话，给大家避坑：弹性工作可能是无固定下班时间；团队年轻化可能是流动率高；抗压能力强可能是长期高压。", now.minusDays(11)),
            createPost("法律顾问小周", "岗位储备可能并没有真实HC", "有些企业长期挂招聘信息，实际并不急招，只是收简历建人才池。面试前可以直接问：当前是否有明确headcount、预计入职时间。", now.minusDays(10)),
            createPost("互联网转行者", "扁平管理听起来好，实际上可能职责边界模糊", "我之前在一家创业团队，扁平管理导致谁都能给你派活，最后KPI却只压在个人身上。建议入职前确认汇报关系和职责范围。", now.minusDays(9)),
            createPost("求职小白", "兼职刷单招聘：从垫付小额到连环充值", "朋友被拉进所谓兼职群，前两单返利正常，后面要求连续垫付几千才能提现，这是经典连环刷单骗局。", now.minusDays(8)),
            createPost("打工人老杨", "试用期不交社保是合法的吗？", "某公司说转正后再统一缴纳社保，试用期先不交。查了下政策，试用期也应依法缴纳社保。有人有维权经验吗？", now.minusDays(7)),
            createPost("职场观察员", "求职黑话：六边形战士通常意味着一人多岗", "JD写着希望你是六边形战士，实际就是产品+运营+销售+客服都要做。面试时建议问清楚岗位核心目标和资源支持。", now.minusDays(6)),
            createPost("维权互助群管理员", "被骗签空白合同怎么办？", "有同学入职当天被催着签字，后来发现关键条款被后补。建议保留证据并及时录音取证，必要时向劳动监察部门投诉。", now.minusDays(5)),
            createPost("跨境电商求职者", "高提成岗位实为拉人头，警惕传销式招聘", "面试官几乎不聊业务，只强调拉新奖励和层级返佣，这种模式风险很高。正规岗位应有明确工作内容和薪酬结构。", now.minusDays(4)),
            createPost("应届生阿宁", "求职黑话：狼性文化可能是末位淘汰", "听起来像积极进取，实际可能意味着超高淘汰率和长期内卷，大家怎么看？", now.minusDays(3)),
            createPost("程序员小赵", "外包转正机会大到底真不真？", "不少岗位会说表现好可转正，但没有具体比例和时间。建议让对方写进补充协议，不然很难落地。", now.minusDays(2)),
            createPost("求职安全科普", "一分钟识别诈骗岗位：先收费、催转账、避合同", "给大家一个速查表：1) 入职前收费直接拉黑；2) 催你当天转账要警惕；3) 不给书面合同不要入职。", now.minusDays(1)),
            createPost("职场老兵", "年轻团队成长快也可能是制度不完善", "优点是机会多，缺点是流程和保障可能都在摸索。面试时一定问清楚试用期考核和加班调休制度。", now.minusHours(16)),
            createPost("校园招聘互助", "实习offer要身份证正反面+银行卡短信验证码，正常吗？", "只提供身份证用于背调还算常见，但要验证码明显不合理。验证码等同于资金操作授权，千万不要给。", now.minusHours(8)),
            createPost("反诈宣传员", "警惕内推名额费：真正内推不会向候选人收费", "最近看到有人冒充大厂员工卖内推名额，收费后只发一封模板邮件。正规内推是员工推荐流程，不会对候选人收费。", now.minusHours(2))
        );

        forumPostRepository.saveAll(seeds);
    }

    private ForumPost createPost(String userId, String title, String content, LocalDateTime createdAt) {
        return ForumPost.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .likeCount(0)
                .commentCount(0)
                .favoriteCount(0)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
    }
}
