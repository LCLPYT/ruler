package work.lclpnet.ruler.rule;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.serialization.Codec;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface RuleKey<V, R extends Rule<V>> {

    Identifier identifier();

    V defaultValue();

    R createRule(V initialValue, RuleHandle<V> args);

    Codec<V> valueCodec();
    
    default Codec<Value<V>> valueWrapperCodec() {
        var valueCodec = valueCodec();
        
        return valueCodec.xmap(v -> new Value<>(v, valueCodec), Value::value);
    }
    
    default Value<V> wrappedDefaultValue() {
        return new Value<>(defaultValue(), valueCodec());
    }

    @Nullable
    default SuggestionProvider<ServerCommandSource> getSuggestions() {
        return null;
    }

    @SuppressWarnings("unchecked")
    default RuleHandle<V> castHandle(RuleHandle<Object> handle) {
        return (RuleHandle<V>) handle;
    }

    record Value<V>(V value, Codec<V> codec) {}
}
