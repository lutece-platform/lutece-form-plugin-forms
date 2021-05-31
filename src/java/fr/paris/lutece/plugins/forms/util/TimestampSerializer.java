package fr.paris.lutece.plugins.forms.util;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class TimestampSerializer extends JsonSerializer<Timestamp>
{
    private DateTimeFormatter _fmt = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm" );

    @Override
    public void serialize( Timestamp value, JsonGenerator gen, SerializerProvider serializers ) throws IOException, JsonProcessingException
    {
        // get the timestmap in the default timezone
        ZonedDateTime z = value.toInstant( ).atZone( ZoneId.systemDefault( ) );
        String str = _fmt.format( z );

        gen.writeString( str );
    }
}
