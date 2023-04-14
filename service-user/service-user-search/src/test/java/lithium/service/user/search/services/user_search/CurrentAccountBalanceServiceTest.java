package lithium.service.user.search.services.user_search;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Date;
import lithium.service.user.search.data.entities.CurrentAccountBalance;
import lithium.service.user.search.data.entities.User;
import lithium.service.user.search.data.repositories.user_search.CurrentAccountBalanceRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CurrentAccountBalanceServiceTest {

  public static final String USER_GUID = "test";
  @InjectMocks
  private CurrentAccountBalanceService currentAccountBalanceService;
  @Mock
  private UserService userService;
  @Mock
  private CurrentAccountBalanceRepository currentAccountBalanceRepository;
  @Captor
  ArgumentCaptor<CurrentAccountBalance> balanceArgumentCaptor;

  @Test
  public void shouldUpdateNewBalanceIfNoTimestamp(){
    given(userService.lockingUpdate(eq(USER_GUID))).willReturn(getUser());
    long storedBalance = 0L;
    given(currentAccountBalanceRepository.findByUser(eq(getUser()))).willReturn(getBalance(storedBalance, null));

    long updateBalance = 1L;
    currentAccountBalanceService.updateLockBalance(USER_GUID, updateBalance, null);

    verify(currentAccountBalanceRepository).save(balanceArgumentCaptor.capture());

    Assert.assertThat(balanceArgumentCaptor.getValue().getCurrentAccountBalance(), is(updateBalance));
    Assert.assertThat(balanceArgumentCaptor.getValue().getTimestamp(), is(nullValue()));
  }

  @Test
  public void shouldUpdateNewBalanceIfTimestampNewer(){
    given(userService.lockingUpdate(eq(USER_GUID))).willReturn(getUser());
    long storedBalance = 0L;
    given(currentAccountBalanceRepository.findByUser(eq(getUser()))).willReturn(getBalance(storedBalance, null));

    long updateBalance = 1L;
    Date updateTimestamp = new Date(System.currentTimeMillis());
    currentAccountBalanceService.updateLockBalance(USER_GUID, updateBalance, updateTimestamp);

    verify(currentAccountBalanceRepository).save(balanceArgumentCaptor.capture());

    Assert.assertThat(balanceArgumentCaptor.getValue().getCurrentAccountBalance(), is(updateBalance));
    Assert.assertThat(balanceArgumentCaptor.getValue().getTimestamp(), is(updateTimestamp));
  }

  @Test
  public void shouldNotUpdateBalanceIfTimestampNull(){
    given(userService.lockingUpdate(eq(USER_GUID))).willReturn(getUser());
    long storedBalance = 0L;
    Date storedTimestamp = new Date(System.currentTimeMillis());
    given(currentAccountBalanceRepository.findByUser(eq(getUser()))).willReturn(getBalance(storedBalance, storedTimestamp));

    long updateBalance = 1L;
    currentAccountBalanceService.updateLockBalance(USER_GUID, updateBalance, null);

    verify(currentAccountBalanceRepository, never()).save(balanceArgumentCaptor.capture());
  }

  @Test
  public void shouldNotUpdateBalanceIfTimestampOlder(){
    given(userService.lockingUpdate(eq(USER_GUID))).willReturn(getUser());
    long storedBalance = 0L;
    Date storedTimestamp = new Date(System.currentTimeMillis());
    given(currentAccountBalanceRepository.findByUser(eq(getUser()))).willReturn(getBalance(storedBalance, storedTimestamp));

    long updateBalance = 1L;
    Date updateTimestamp = new Date(System.currentTimeMillis()-10);
    currentAccountBalanceService.updateLockBalance(USER_GUID, updateBalance, updateTimestamp);

    verify(currentAccountBalanceRepository, never()).save(balanceArgumentCaptor.capture());
  }

  @Test
  public void shouldNotUpdateBalanceIfTimestampEquals(){
    given(userService.lockingUpdate(eq(USER_GUID))).willReturn(getUser());
    long storedBalance = 0L;
    Date storedTimestamp = new Date(System.currentTimeMillis());
    given(currentAccountBalanceRepository.findByUser(eq(getUser()))).willReturn(getBalance(storedBalance, storedTimestamp));

    long updateBalance = 1L;
    Date updateTimestamp = storedTimestamp;
    currentAccountBalanceService.updateLockBalance(USER_GUID, updateBalance, updateTimestamp);

    verify(currentAccountBalanceRepository, never()).save(balanceArgumentCaptor.capture());
  }

  @Test
  public void shouldUpdateBalanceIfTimestampNewer(){
    given(userService.lockingUpdate(eq(USER_GUID))).willReturn(getUser());
    long storedBalance = 0L;
    Date storedTimestamp = new Date(System.currentTimeMillis());
    given(currentAccountBalanceRepository.findByUser(eq(getUser()))).willReturn(getBalance(storedBalance, storedTimestamp));

    long updateBalance = 1L;
    Date updateTimestamp = new Date(System.currentTimeMillis()+1);
    currentAccountBalanceService.updateLockBalance(USER_GUID, updateBalance, updateTimestamp);

    verify(currentAccountBalanceRepository).save(balanceArgumentCaptor.capture());

    Assert.assertThat(balanceArgumentCaptor.getValue().getCurrentAccountBalance(), is(updateBalance));
    Assert.assertThat(balanceArgumentCaptor.getValue().getTimestamp(), is(updateTimestamp));
  }


  private static User getUser(){
    return User.builder()
        .guid(USER_GUID).build();
  }

  private CurrentAccountBalance getBalance(long balanceAmount, Date timestamp){
    return CurrentAccountBalance.builder()
        .currentAccountBalance(balanceAmount)
        .timestamp(timestamp)
        .build();
  }

}
