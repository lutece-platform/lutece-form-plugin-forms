package fr.paris.lutece.plugins.forms.service.lock;

import fr.paris.lutece.plugins.forms.exception.LockException;

public interface LuceneLockManager {

    /**
     * acquire a lock
     *
     * @param indexName the index name
     * @param timeoutMs the timout in ms
     * @return LockResult
     * @throws LockException
     */
    LockResult acquireLock(String indexName, long timeoutMs) throws LockException;

    /**
     * release a lock
     *
     * @param lockResult the LockResult
     * @throws LockException
     */
    void releaseLock(LockResult lockResult) throws LockException;

    /**
     *
     * @param lockResult the LockResult
     * @param timeoutMs the timout in ms to extend from the current time
     * @return LockResult
     * @throws LockException
     */
    LockResult refreshLock(LockResult lockResult, long timeoutMs) throws LockException;

    /**
     * reset all the lock
     */
    void close();

}
