package dev.dreamhopping.coordinate

import dev.dreamhopping.coordinate.mappings.impl.mcp.provider.MCPMappingProvider
import dev.dreamhopping.coordinate.minecraft.provider.MinecraftVersionProvider
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlin.system.measureTimeMillis

/**
 * Coordinate is a Minecraft Mapping Viewer with support for MCP, Yarn and Mojang mappings
 * @author Conor Byrne (dreamhopping)
 */
@ExperimentalSerializationApi
class Coordinate {
    companion object {
        val json = Json {
            isLenient = true
            ignoreUnknownKeys = true
        }

        @JvmStatic
        fun main(args: Array<String>) {
            println("Hello World")

            runBlocking {
                val minecraftVersionProvider = MinecraftVersionProvider()
                minecraftVersionProvider.fetchManifest()

                val mcpMappingProvider = MCPMappingProvider()
                val mc189 = minecraftVersionProvider.getVersion("1.8.9")
                    ?: throw Exception("Couldn't get version 1.8.9 from manifest!")

                mcpMappingProvider.prepareForUsage()
                val mappings = mcpMappingProvider.fetchLatestMappings(mc189)

                val timeToFindMethods = measureTimeMillis {
                    mappings.methods.keys.forEach {
                        mappings.methods[it]
                    }
                }

                val timeToFindClasses = measureTimeMillis {
                    mappings.classes.keys.forEach {
                        mappings.classes[it]
                    }
                }

                val timeToFindFields = measureTimeMillis {
                    mappings.fields.keys.forEach {
                        mappings.fields[it]
                    }
                }


                println("Took ${timeToFindMethods}ms to find ${mappings.methods.keys.size} methods (mapped)")
                println("Took ${timeToFindClasses}ms to find ${mappings.classes.keys.size} classes (mapped)")
                println("Took ${timeToFindFields}ms to find ${mappings.fields.keys.size} fields (mapped)")
            }
        }
    }
}
