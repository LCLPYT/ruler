package work.lclpnet.ruler.rule;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.serialization.Codec;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface RuleKey<V, R extends Rule<V>> {

    ResourceLocation identifier();

    V defaultValue();

    R createRule(V initialValue, RuleHandle<V> args);

    Codec<V> valueCodec();
    
    default Codec<Value<V>> valueWrapperCodec() {
        var valueCodec = valueCodec();
        
        return valueCodec.xmap(Value::new, Value::value);
    }
    
    default Value<V> wrappedDefaultValue() {
        return new Value<>(defaultValue());
    }

    @Nullable
    default SuggestionProvider<CommandSourceStack> getSuggestions() {
        return null;
    }

    @SuppressWarnings("unchecked")
    default RuleHandle<V> castHandle(RuleHandle<Object> handle) {
        return (RuleHandle<V>) handle;
    }

    record Value<V>(V value) {}
}
