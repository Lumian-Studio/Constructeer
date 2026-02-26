package xyz.lumian.constructeer.registry;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.CteerDefine;
import xyz.lumian.constructeer.util.FreezableMap;
import xyz.lumian.constructeer.util.ItemFactory;

import java.io.Closeable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;



//**********************************************************************************************************************
public final class CteerItemRegistry
    implements IBootstrap
{
    //******************************************************************************************************************
    public record ScopedTab(ResourceKey<CreativeModeTab> tabKey)
        implements Closeable
    {
        //**************************************************************************************************************
        public ScopedTab { CteerItemRegistry.TAB_MANAGER.setCurrentTab(tabKey); }
        
        //**************************************************************************************************************
        @Override public void close() { CteerItemRegistry.TAB_MANAGER.setCurrentTab(null); }
    }
    
    //------------------------------------------------------------------------------------------------------------------
    private static final class TabManager
    {
        //**************************************************************************************************************
        private final Map<ResourceKey<CreativeModeTab>, Set<Item>> items = new Reference2ObjectArrayMap<>();
        
        private @Nullable ResourceKey<CreativeModeTab> currentTab;
        private @Nullable ResourceKey<CreativeModeTab> lastTab;
        private @Nullable Set<Item>                    activeSet;
        
        //**************************************************************************************************************
        public @Nullable ResourceKey<CreativeModeTab> getCurrentTab() { return this.currentTab; }
        
        //==============================================================================================================
        public Stream<Map.Entry<ResourceKey<CreativeModeTab>, Set<Item>>> stream()
        {
            return this.items.entrySet().stream();
        }
        
        //==============================================================================================================
        public void setCurrentTab(final @Nullable ResourceKey<CreativeModeTab> currentTab)
        {
            this.currentTab = currentTab;
        }
        
        //==============================================================================================================
        public void register(final Item item)
        {
            if (this.currentTab != null)
            {
                if (this.lastTab != this.currentTab)
                {
                    this.lastTab   = this.currentTab;
                    this.activeSet = this.items.computeIfAbsent(this.currentTab, (k -> new ObjectArraySet<>()));
                }
                
                Objects.requireNonNull(this.activeSet).add(item);
            }
        }
    }
    
    //******************************************************************************************************************
    /// Contains all items that were registered with any of this class' register methods and which namespaces start with
    /// "constructeer".
    public static FreezableMap<Identifier, Item> BY_ID = new FreezableMap<>(new Object2ObjectOpenHashMap<>());
    
    //==================================================================================================================
    public static final ResourceKey<CreativeModeTab> MAIN_TAB_KEY = createTabKey("main");
    public static final CreativeModeTab              MAIN_TAB     = registerTab(
        CteerItemRegistry.MAIN_TAB_KEY,
        Component.translatable(CteerDefine.formatId("itemGroup.%s.main")),
        (() -> new ItemStack(CteerItemRegistry.TROPHY)));
    
    private static TabManager TAB_MANAGER = new TabManager();
    
    //==================================================================================================================
    public static final Item TROPHY = register("trophy", Item::new, (new Item.Properties()).stacksTo(1));
    
    //******************************************************************************************************************
    public static ScopedTab usingTab(final ResourceKey<CreativeModeTab> tabKey) { return new ScopedTab(tabKey); }
    
    //==================================================================================================================
    public static <T extends Item> T register(final ResourceKey<Item> key,
                                              final ItemFactory<T>    factory,
                                              final Item.Properties   initProperties)
    {
        final T item = factory.generate(initProperties.setId(key));
        
        if (key.identifier().getNamespace().startsWith(CteerDefine.MOD_ID))
        {
            if (CteerItemRegistry.BY_ID.put(key.identifier(), item) != null)
            {
                throw new IllegalStateException("Duplicate item " + key.identifier());
            }
        }
        
        CteerItemRegistry.TAB_MANAGER.register(item);
        return CteerRegistries.register(BuiltInRegistries.ITEM, key, item);
    }
    
    public static <T extends Item> T registerWithTab(final ResourceKey<Item>                      key,
                                                     final ItemFactory<T>                         factory,
                                                     final Item.Properties                        initProperties,
                                                     final @Nullable ResourceKey<CreativeModeTab> tabKey)
    {
        final T item;
        {
            final ResourceKey<CreativeModeTab> prev_tab = CteerItemRegistry.TAB_MANAGER.getCurrentTab();
            
            CteerItemRegistry.TAB_MANAGER.setCurrentTab(Objects.requireNonNull(tabKey));
            item = CteerItemRegistry.register(key, factory, initProperties);
            CteerItemRegistry.TAB_MANAGER.setCurrentTab(prev_tab);
        }
        
        return item;
    }
    
    public static <T extends Item> T register(final Identifier      id,
                                              final ItemFactory<T>  factory,
                                              final Item.Properties initProperties)
    {
        return CteerItemRegistry.register(ResourceKey.create(Registries.ITEM, id), factory, initProperties);
    }
    
    public static <T extends Item> T registerWithTab(final Identifier                   id,
                                                     final ItemFactory<T>               factory,
                                                     final Item.Properties              initProperties,
                                                     final ResourceKey<CreativeModeTab> tabKey)
    {
        return CteerItemRegistry.registerWithTab(ResourceKey.create(Registries.ITEM, id), factory, initProperties,
                                                 tabKey);
    }
    
    public static <T extends Item> T register(final String          name,
                                              final ItemFactory<T>  factory,
                                              final Item.Properties initProperties)
    {
        return CteerItemRegistry.register(CteerDefine.id(name), factory, initProperties);
    }
    
    public static <T extends Item> T registerWithTab(final String                       name,
                                                     final ItemFactory<T>               factory,
                                                     final Item.Properties              initProperties,
                                                     final ResourceKey<CreativeModeTab> tabKey)
    {
        return CteerItemRegistry.registerWithTab(CteerDefine.id(name), factory, initProperties, tabKey);
    }
    
    //==================================================================================================================
    public static ResourceKey<CreativeModeTab> createTabKey(final String name)
    {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, CteerDefine.id(name));
    }
    
    public static CreativeModeTab registerTab(final ResourceKey<CreativeModeTab> key, final CreativeModeTab tab)
    {
        return CteerRegistries.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, tab);
    }
    
    public static CreativeModeTab registerTab(final ResourceKey<CreativeModeTab> key,
                                              final Component                    title,
                                              final Supplier<ItemStack>          icon)
    {
        return CteerRegistries.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, FabricItemGroup.builder()
            .title(title)
            .icon(icon)
            .build());
    }
    
    //******************************************************************************************************************
    @Override
    public void freeze(final BootstrapReport report)
    {
        CteerItemRegistry.BY_ID.freeze();
        CteerItemRegistry.TAB_MANAGER.stream().forEach(e ->
        {
            final Set<Item> items = e.getValue();
            
            if (!items.isEmpty())
            {
                ItemGroupEvents.modifyEntriesEvent(e.getKey())
                    .register(entries -> e.getValue().forEach(entries::accept));
                report.report(() -> "registered %s items for creative tab '%s': %s"
                    .formatted(items.size(), e.getKey().identifier(), Arrays.toString(items.stream()
                        .map(BuiltInRegistries.ITEM::getKey)
                        .toArray(Identifier[]::new))));
            }
        });
        
        //noinspection DataFlowIssue
        CteerItemRegistry.TAB_MANAGER = null;
    }
}
