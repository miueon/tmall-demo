package com.tmallspringboot.demo.realm;

import com.tmallspringboot.demo.pojo.User;
import com.tmallspringboot.demo.service.UserService;
import lombok.val;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

public class JPARealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;
    // 授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo s = new SimpleAuthorizationInfo();
        return s;
    }

    // 认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        val username = authenticationToken.getPrincipal().toString();
        val usr = userService.getByName(username);
        val passwordInDB = usr.getPassword();
        var salt = usr.getSalt();
        if (salt == null) {
            salt = authenticationHelper(usr, passwordInDB, userService);
        }
        return new SimpleAuthenticationInfo(username, passwordInDB,
                ByteSource.Util.bytes(salt), getName());
    }

    public static String  authenticationHelper(User usr, String passwordInDB, UserService userService) {
        val new_salt = new SecureRandomNumberGenerator().nextBytes().toString();

        int times = 2;
        val algName = "md5"; // how to pass these two value

        val encodedPassword = new SimpleHash(algName, passwordInDB, new_salt, times).toString();
        usr.setSalt(new_salt);
        usr.setPassword(encodedPassword);
        userService.add(usr);
        return new_salt;
    }
}
