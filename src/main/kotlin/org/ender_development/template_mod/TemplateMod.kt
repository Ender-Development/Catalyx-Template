package org.ender_development.template_mod

import net.minecraft.creativetab.CreativeTabs
import net.minecraftforge.fml.common.Mod
import org.ender_development.catalyx.core.CatalyxSettings
import org.ender_development.catalyx.core.ICatalyxMod
import org.ender_development.templatemod.Reference

@Mod(
    modid = Reference.MODID,
    name = Reference.MOD_NAME,
    version = Reference.VERSION,
    dependencies = ICatalyxMod.CATALYX_ADDON,
    modLanguageAdapter = ICatalyxMod.MOD_LANGUAGE_ADAPTER,
)
object TemplateMod : ICatalyxMod {
    override val modSettings = CatalyxSettings(Reference.MODID, CreativeTabs.MISC, this, true)
}
