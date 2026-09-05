package org.ender_development.template_mod

import org.spongepowered.asm.mixin.Mixins
import org.spongepowered.asm.mixin.connect.IMixinConnector

/**
 * Example implementation of an [IMixinConnector].
 * This NEEDS to be a class, NOT an object!
 */
class MixinConnector: IMixinConnector {
    override fun connect() {
        Mixins.addConfiguration("mixins.dummy.json")
    }
}
