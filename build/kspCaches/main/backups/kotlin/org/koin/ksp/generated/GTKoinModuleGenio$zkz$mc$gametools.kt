package org.koin.ksp.generated

import org.koin.dsl.*


internal val io_zkz_mc_gametools_GTKoinModule = module {
	single(qualifier=null) { io.zkz.mc.gametools.util.GTColors() } bind(net.kyori.adventure.text.minimessage.tag.resolver.TagResolver::class)
}
internal val io.zkz.mc.gametools.GTKoinModule.module : org.koin.core.module.Module get() = io_zkz_mc_gametools_GTKoinModule