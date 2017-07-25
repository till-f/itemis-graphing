package de.itemis.graphstreamwrapper;

import java.util.List;

public interface IAttachmentContainer
{

    Attachment addAttachment(String id, double radius, double degree);

    List<Attachment> getAttachments();

}
