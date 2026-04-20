package fr.paris.lutece.plugins.forms.service.lock;

/**
 * Lucene-specific flavour of {@link FormsDistributedLockManager}, kept for backward
 * compatibility with existing callers that inject by name {@code "forms.luceneLockManager"}.
 * New code should depend on {@link FormsDistributedLockManager} directly.
 */
public interface LuceneLockManager extends FormsDistributedLockManager
{
}
