package org.ender_development.template_mod

import org.spongepowered.asm.mixin.Mixins
import org.spongepowered.asm.mixin.connect.IMixinConnector

object MixinConnector: IMixinConnector {
    override fun connect() {
        Mixins.addConfiguration("path/to/your/mixin.json")
    }
}
