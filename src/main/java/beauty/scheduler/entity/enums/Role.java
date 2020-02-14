package beauty.scheduler.entity.enums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum Role {
    ROLE_ANONYMOUS("all,notAuthenticated,nonstaff", ""),
    ROLE_USER("all,authenticated,nonstaff,front", "i18n.USER"),
    ROLE_MASTER("all,authenticated,staff,front", "i18n.MASTER"),
    ROLE_ADMIN("all,authenticated,staff,admin,front", "i18n.ADMIN"),
    //these roles are for annotating convenience in @EndpointMethod:
    all("", ""),
    notAuthenticated("", ""),
    authenticated("", ""),
    staff("", ""),
    nonstaff("", "");

    private static final Logger LOGGER = LoggerFactory.getLogger(Role.class);

    private String tags;
    private String i18n;

    Role(String tags, String i18n) {
        this.tags = tags;
        this.i18n = i18n;
    }

    public String getI18n() {
        return i18n;
    }

    public boolean hasTag(String tag) {
        return Arrays.asList(this.tags.split(","))
                .contains(tag);
    }

    public static List<Role> getAllByTag(String tag) {
        return Arrays.stream(values())
                .filter(role -> role.hasTag(tag))
                .collect(Collectors.toList());
    }

    public static List<Role> getAllByRoleTag(Role roleTag) {
        if ("".equals(roleTag.getTags())) {
            return getAllByTag(roleTag.name());
        } else {
            return Arrays.asList(roleTag);
        }
    }

    //make safer parsing from JSON
    public static Role lookupNotNull(String name) {
        try {
            return Role.valueOf(name);
        } catch (IllegalArgumentException e) {
            LOGGER.debug("Role " + name + " not found");
            return Role.ROLE_ANONYMOUS;
        }
    }

    public static Optional<Role> lookupOptional(String name) {
        try {
            return Optional.of(Role.valueOf(name));
        } catch (IllegalArgumentException e) {
            LOGGER.debug("Role " + name + " not found");
            return Optional.empty();
        }
    }

    //for new registering users:
    public static final Role DEFAULT_ROLE = ROLE_USER;

    public String getTags() {
        return this.tags;
    }

    @Override
    public String toString() {
        return name();
    }
}