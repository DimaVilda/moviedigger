package com.backbase.moviesdigger.util;

import com.backbase.moviesdigger.TestUtils;
import com.backbase.moviesdigger.utils.TokenMethodsUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.backbase.moviesdigger.utils.consts.JwtClaimsConst.PREFERRED_USERNAME_CLAIM;
import static org.apache.logging.log4j.ThreadContext.isEmpty;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TokenMethodsUtilTest {

    @InjectMocks
    private TokenMethodsUtil tokenMethodsUtil;


    @Test
    void testGetUserTokenClaimValueWithValidToken() {
        String claimValue = "dima";
        assertThat(tokenMethodsUtil.getUserTokenClaimValue(TestUtils.accessToken, PREFERRED_USERNAME_CLAIM), is(claimValue));
    }

    @Test
    void testGetUserTokenClaimValueWithMalformedToken() {
        String malformedToken = "malformedToken";
        assertNull(tokenMethodsUtil.getUserTokenClaimValue(malformedToken, "anyClaim"));
    }

    @Test
    void testGetUserTokenClaimValueWithTokenMissingClaim() {
        assertThat(tokenMethodsUtil.getUserTokenClaimValue(TestUtils.accessToken, "missingTestClaim"), isEmpty());
    }
}
