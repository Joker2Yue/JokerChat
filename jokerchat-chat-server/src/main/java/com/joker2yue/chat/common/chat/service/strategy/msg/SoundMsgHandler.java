package com.joker2yue.chat.common.chat.service.strategy.msg;

import com.joker2yue.chat.common.chat.dao.MessageDao;
import com.joker2yue.chat.common.chat.domain.entity.Message;
import com.joker2yue.chat.common.chat.domain.entity.msg.MessageExtra;
import com.joker2yue.chat.common.chat.domain.entity.msg.SoundMsgDTO;
import com.joker2yue.chat.common.chat.domain.enums.MessageTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Description:图片消息
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-06-04
 */
@Component
public class SoundMsgHandler extends AbstractMsgHandler<SoundMsgDTO> {
    @Autowired
    private MessageDao messageDao;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.SOUND;
    }

    @Override
    public void saveMsg(Message msg, SoundMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
        extra.setSoundMsgDTO(body);
        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        return msg.getExtra().getSoundMsgDTO();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "语音";
    }

    @Override
    public String showContactMsg(Message msg) {
        return "[语音]";
    }
}