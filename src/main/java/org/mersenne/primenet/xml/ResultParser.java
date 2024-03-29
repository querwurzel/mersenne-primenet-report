package org.mersenne.primenet.xml;

import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.mersenne.primenet.xml.ResultParser.ResultSchema.*;

@Component
public class ResultParser {

    private static final XMLInputFactory factory = XMLInputFactory.newFactory();

    static {
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_COALESCING, false);
    }

    public Results parseResults(InputStream stream) throws XMLStreamException {
        final XMLEventReader reader = factory.createXMLEventReader(stream);
        final List<ResultLine> lines = new ArrayList<>();
        String importDate = null;

        try {
            ResultLine result = new ResultLine();

            while (reader.hasNext()) {
                final XMLEvent event = reader.nextEvent();

                if (event.isStartElement()) {
                    final StartElement element = event.asStartElement();
                    final QName tag = element.getName();

                    if (RESULTS.equals(tag)) {
                        importDate = element.getAttributeByName(DTSTART).getValue();
                        continue;
                    }

                    if (RESULT.equals(tag)) {
                        result.setExponent(element.getAttributeByName(EXPONENT).getValue());
                        continue;
                    }

                    if (USERNAME.equals(tag)) {
                        result.setUserName(reader.getElementText());
                        continue;
                    }

                    if (COMPUTERNAME.equals(tag)) {
                        result.setComputerName(reader.getElementText());
                        continue;
                    }

                    if (RESULTTYPE.equals(tag)) {
                        result.setResultType(reader.getElementText());
                        continue;
                    }

                    if (DATERECEIVED.equals(tag)) {
                        result.setDateReceived(reader.getElementText());
                        continue;
                    }

                    if (ASSIGNMENTAGE.equals(tag)) {
                        result.setAssignmentAge(reader.getElementText());
                        continue;
                    }

                    if (GHZDAYS.equals(tag)) {
                        result.setGhzDays(reader.getElementText());
                        continue;
                    }

                    if (MESSAGE.equals(tag)) {
                        result.setMessage(reader.getElementText());
                        continue;
                    }
                }

                if (event.isEndElement()) {
                    final EndElement element = event.asEndElement();
                    final QName tag = element.getName();

                    if (RESULT.equals(tag)) {
                        lines.add(result);
                        result = new ResultLine();
                    }
                }
            }
        } finally {
            reader.close();
        }

        return new Results(importDate, lines);
    }

    protected static final class ResultSchema {

        protected static final QName RESULTS = QName.valueOf("results");
        protected static final QName DTSTART = QName.valueOf("dtStart");
        protected static final QName RESULT = QName.valueOf("result");
        protected static final QName EXPONENT = QName.valueOf("exponent");
        protected static final QName USERNAME = QName.valueOf("UserName");
        protected static final QName COMPUTERNAME = QName.valueOf("ComputerName");
        protected static final QName RESULTTYPE = QName.valueOf("ResultType");
        protected static final QName DATERECEIVED = QName.valueOf("DateReceived");
        protected static final QName ASSIGNMENTAGE = QName.valueOf("AssignmentAge");
        protected static final QName MESSAGE = QName.valueOf("Message");
        protected static final QName GHZDAYS = QName.valueOf("GHzDays");

        private ResultSchema() {}
    }
}
