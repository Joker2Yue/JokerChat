package com.joker2yue.chat.common.common.event.listener;

import com.joker2yue.chat.common.chat.dao.MessageDao;
import com.joker2yue.chat.common.chat.dao.MessageMarkDao;
import com.joker2yue.chat.common.chat.domain.dto.ChatMessageMarkDTO;
import com.joker2yue.chat.common.chat.domain.entity.Message;
import com.joker2yue.chat.common.chat.domain.enums.MessageMarkTypeEnum;
import com.joker2yue.chat.common.chat.domain.enums.MessageTypeEnum;
import com.joker2yue.chat.common.common.domain.enums.IdempotentEnum;
import com.joker2yue.chat.common.common.event.MessageMarkEvent;
import com.joker2yue.chat.common.user.domain.enums.ItemEnum;
import com.joker2yue.chat.common.user.service.IUserBackpackService;
import com.joker2yue.chat.common.user.service.adapter.WSAdapter;
import com.joker2yue.chat.common.user.service.impl.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

/**
 * 消息标记监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class MessageMarkListener {
    @Autowired
    private MessageMarkDao messageMarkDao;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private IUserBackpackService iUserBackpackService;
    @Autowired
    private PushService pushService;

    @Async
    @TransactionalEventListener(classes = MessageMarkEvent.class, fallbackExecution = true)
    public void changeMsgType(MessageMarkEvent event) {
        ChatMessageMarkDTO dto = event.getDto();
        Message msg = messageDao.getById(dto.getMsgId());
        if (!Objects.equals(msg.getType(), MessageTypeEnum.TEXT.getType())) {//普通消息才需要升级
            return;
        }
        //消息被标记次数
        Integer markCount = messageMarkDao.getMarkCount(dto.getMsgId(), dto.getMarkType());
        MessageMarkTypeEnum markTypeEnum = MessageMarkTypeEnum.of(dto.getMarkType());
        if (markCount < markTypeEnum.getRiseNum()) {
            return;
        }
        if (MessageMarkTypeEnum.LIKE.getType().equals(dto.getMarkType())) {//尝试给用户发送一张徽章
            iUserBackpackService.acquireItem(msg.getFromUid(), ItemEnum.LIKE_BADGE.getId(), IdempotentEnum.MSG_ID, msg.getId().toString());
        }
    }

    @Async
    @TransactionalEventListener(classes = MessageMarkEvent.class, fallbackExecution = true)
    public void notifyAll(MessageMarkEvent event) {//后续可做合并查询，目前异步影响不大
        ChatMessageMarkDTO dto = event.getDto();
        Integer markCount = messageMarkDao.getMarkCount(dto.getMsgId(), dto.getMarkType());
        pushService.sendPushMsg(WSAdapter.buildMsgMarkSend(dto, markCount));
    }

}