package guru.qa.rococo.model;

import javax.annotation.Nonnull;

public record ErrorJson(@Nonnull String type,
                        @Nonnull String title,
                        int status,
                        @Nonnull String detail,
                        @Nonnull String instance) {
}