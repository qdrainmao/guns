package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.UserInfoModel;
import com.stylefeng.guns.api.user.UserModel;
import com.stylefeng.guns.core.util.MD5Util;
import com.stylefeng.guns.rest.common.persistence.dao.MoocUserTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocUserT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Service(interfaceClass = UserAPI.class)
public class UserServiceImpl implements UserAPI {

    @Autowired
    private MoocUserTMapper moocUserTMapper;

    @Override
    public int login(String username, String password) {
        //根据登录账号获取数据库信息
        MoocUserT moocUserT = new MoocUserT();
        moocUserT.setUserName(username);
        MoocUserT result = moocUserTMapper.selectOne(moocUserT);

        //获取到的结果，然后与加密以后的密码做匹配
        if (result!=null && result.getUuid()>0){
            String md5Password = MD5Util.encrypt(password);
            if (result.getUserPwd().equals(md5Password)){
                return result.getUuid();
            }
        }
        return 0;
    }

    @Override
    public boolean register(UserModel userModel) {
        //将注册信息实体转换为数据实体
        MoocUserT moocUserT = new MoocUserT();
        moocUserT.setUserName(userModel.getUsername());

        //数据加密，【MD5混淆加密 + 盐值即一个随机值  -> shiro加密】，这里我们省略处理没有盐值
        //MD5是不能被反解密的，但花时间也能解密，所以加上盐值加上密码后再MD5加密能做到更安全
        String md5Password = MD5Util.encrypt(userModel.getPassword());
        moocUserT.setUserPwd(md5Password);
        moocUserT.setEmail(userModel.getEmail());
        moocUserT.setAddress(userModel.getAddress());
        moocUserT.setUserPhone(userModel.getPhone());
        //将数据实体存入数据库
        Integer insert = moocUserTMapper.insert(moocUserT);
        if (insert>0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean checkUsername(String username) {
        EntityWrapper<MoocUserT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("user_name", username);
        Integer result = moocUserTMapper.selectCount(entityWrapper);
        if (result!=null && result>0){
            return false;
        }else {
            return true;
        }
    }

    private UserInfoModel toUserInfoModel(MoocUserT moocUserT){
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setUsername(moocUserT.getUserName());
        userInfoModel.setUpdateTime(moocUserT.getUpdateTime().getTime());
        userInfoModel.setSex(moocUserT.getUserSex());
        userInfoModel.setPhone(moocUserT.getUserPhone());
        userInfoModel.setNickname(moocUserT.getNickName());
        userInfoModel.setLifeState(""+moocUserT.getLifeState());
        userInfoModel.setHeadAddress(moocUserT.getAddress());
        userInfoModel.setEmail(moocUserT.getEmail());
        userInfoModel.setCreateTime(moocUserT.getBeginTime().getTime());
        userInfoModel.setBirthday(moocUserT.getBirthday());
        userInfoModel.setBiography(moocUserT.getBiography());
        userInfoModel.setAddress(moocUserT.getAddress());
        userInfoModel.setUuid(moocUserT.getUuid());
        return userInfoModel;
    }

    private MoocUserT toMoocUserT(UserInfoModel userInfoModel){
        MoocUserT moocUserT = new MoocUserT();
        moocUserT.setUuid(userInfoModel.getUuid());
        moocUserT.setUserSex(userInfoModel.getSex());
        moocUserT.setUpdateTime(new Date(userInfoModel.getUpdateTime()));
        moocUserT.setNickName(userInfoModel.getNickname());
        moocUserT.setLifeState(Integer.valueOf(userInfoModel.getLifeState()));
        moocUserT.setHeadUrl(userInfoModel.getHeadAddress());
        moocUserT.setBirthday(userInfoModel.getBirthday());
        moocUserT.setBiography(userInfoModel.getBiography());
        moocUserT.setBeginTime(new Date(userInfoModel.getCreateTime()));
        moocUserT.setEmail(userInfoModel.getEmail());
        moocUserT.setAddress(userInfoModel.getAddress());
        moocUserT.setUserPhone(userInfoModel.getPhone());
        moocUserT.setUserName(userInfoModel.getUsername());

        return moocUserT;
    }

    @Override
    public UserInfoModel getUserInfo(int uuid) {
        //根据主键查询用户信息[MoocUserT]
        MoocUserT moocUserT = moocUserTMapper.selectById(uuid);
        UserInfoModel userInfoModel = toUserInfoModel(moocUserT);
        return userInfoModel;
    }

    @Override
    public UserInfoModel updateUserInfo(UserInfoModel userInfoModel) {
        //将传入的数据转换为MoocUserT
        MoocUserT moocUserT = toMoocUserT(userInfoModel);
        //将数据存入数据库
        Integer isSuccess = moocUserTMapper.updateById(moocUserT);
        if (isSuccess==1){
            //按照ID将用户信息查出来
            UserInfoModel userInfo = getUserInfo(userInfoModel.getUuid());
            return userInfo;
        }else {
            return userInfoModel;
        }
    }
}
