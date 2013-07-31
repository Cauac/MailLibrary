package protocol.imap;

interface IMAPTags {

    String LOGIN = "a001";
    String SELECT = "a002";
    String FETCH_OPEN = "a003";
    String FETCH_SIZE = "a004";
    String FETCH_MAILBOX_SIZE = "a005";
    String FETCH_INFO = "a006";
    String STORE = "a007";
    String EXPUNGE = "a008";
    String NOOP = "a009";
    String LOGOUT = "a010";
    String STATUS = "a011";
}
