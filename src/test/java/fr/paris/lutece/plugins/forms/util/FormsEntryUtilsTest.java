package fr.paris.lutece.plugins.forms.service.entrytype.util;

import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.util.FormsEntryUtils;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.ReferenceList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;


public class FormsEntryUtilsTest extends LuteceTestCase {
    private static final String DEFAULT_QUESTION_DESCRIPTION = "default_question_description";

    private static final int ENTRY_TYPE_TEXT_ID = 106;
    private static final String ENTRY_TYPE_TITLE = "default_entry_type_title";

    private static final String BEAN_NAME_ENTRY_TYPE_TEXT = "forms.entryTypeText";


    public void testCreateEntryByType() {

        Entry entryByType = FormsEntryUtils.createEntryByType(120);
        assertNotNull(entryByType);
        EntryType entryType = entryByType.getEntryType();
        assertNotNull(entryType);

        assertEquals("Nombre", entryType.getTitle());

        Entry entryByType1 = FormsEntryUtils.createEntryByType(-1);
        assertNull(entryByType1);

    }

    public void testGetIndexFieldInTheFieldList() {
        int idx = FormsEntryUtils.getIndexFieldInTheFieldList(10, Collections.emptyList());
        assertEquals(0, idx);

        int indexFieldInTheFieldList = FormsEntryUtils.getIndexFieldInTheFieldList(2, createQuestion().getEntry().getFields());
        assertEquals(1, indexFieldInTheFieldList);

    }

    public void testInitRefListEntryType() {
        ReferenceList referenceItems = FormsEntryUtils.initRefListEntryType();
        assertEquals(18, referenceItems.size());
    }

    public void testInitListEntryType() {

        List<EntryType> entryTypes = FormsEntryUtils.initListEntryType();
        assertFalse(entryTypes.isEmpty());
    }

    public void testFindFieldByCode() {

        final Question q = createQuestion();

        Field fieldByCode = FormsEntryUtils.findFieldByCode(q.getEntry(), "CODE 2");
        assertNotNull(fieldByCode);
        assertEquals("TITLE 2", fieldByCode.getTitle());
        assertEquals(2, fieldByCode.getIdField());
    }

    private Question createQuestion() {

        EntryType entryTypeText = new EntryType();
        entryTypeText.setIdType(ENTRY_TYPE_TEXT_ID);
        entryTypeText.setTitle(ENTRY_TYPE_TITLE);
        entryTypeText.setBeanName(BEAN_NAME_ENTRY_TYPE_TEXT);

        Entry entry = new Entry();
        entry.setIndexed(true);
        entry.setEntryType(entryTypeText);
        final List<Field> fields = new ArrayList<>();

        IntStream.range(1, 4).forEach(i -> {
            final Field field = new Field();
            field.setIdField(i);
            field.setCode("CODE " + i);
            field.setTitle("TITLE " + i);
            fields.add(field);
        });

        entry.setFields(fields);

        Question question = new Question();
        question.setDescription(DEFAULT_QUESTION_DESCRIPTION);
        question.setId(1);
        question.setEntry(entry);

        return question;
    }
}
