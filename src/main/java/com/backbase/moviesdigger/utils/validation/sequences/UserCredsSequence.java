package com.backbase.moviesdigger.utils.validation.sequences;

import com.backbase.moviesdigger.utils.validation.groups.UserPasswordGroup;
import com.backbase.moviesdigger.utils.validation.groups.UserNameGroup;
import jakarta.validation.GroupSequence;

/**
 * An interface to specify order for groups validation
 */
@GroupSequence({
        UserNameGroup.class,
        UserPasswordGroup.class
})
public interface UserCredsSequence {
}
