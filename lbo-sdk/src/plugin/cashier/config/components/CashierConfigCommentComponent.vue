<template>
  <div data-test-id="cashier-config-comment">
    <div class="text-muted bold-text pa-2">{{comment.name}}</div>
    <small class="mt-auto pa-2 text-muted">
      <b>{{$translate('UI_NETWORK_ADMIN.CASHIER.PLAYER.MIN')}}:</b> {{comment.limits ? comment.limits.minAmount : ''}}&nbsp;
      <b>{{$translate('UI_NETWORK_ADMIN.CASHIER.PLAYER.MAX')}}:</b> {{comment.limits ? comment.limits.maxAmount : ''}}&nbsp;
      <b ng-if="comment.userBalanceCents">{{$translate('UI_NETWORK_ADMIN.CASHIER.PLAYER.BAL')}}:</b> {{comment.userBalanceCents}}&nbsp;
    </small>
    <small class="mt-auto pa-2 text-muted"><b>{{$translate('UI_NETWORK_ADMIN.CASHIER.PLAYER.PROCESSOR')}}:</b> {{comment.processor ? comment.processor.description : ''}}</small>
    <div class="ml-auto pa-2">
      <table class="table table-sm" data-test-id="tbl-comment-info">
        <thead>
        <tr class="description bold-text">
          <th>&nbsp;</th>
          <th>{{$translate('UI_NETWORK_ADMIN.CASHIER.PLAYER.MAX_AMT')}}</th>
          <th>{{$translate('UI_NETWORK_ADMIN.CASHIER.PLAYER.MAX_TRN')}}</th>
        </tr>
        </thead>
        <tbody>
        <tr class="description-sm">
          <td>
            <div>{{$translate('UI_NETWORK_ADMIN.CASHIER.PLAYER.PD')}}</div>
            <div>{{$translate('UI_NETWORK_ADMIN.CASHIER.PLAYER.PW')}}</div>
            <div>{{$translate('UI_NETWORK_ADMIN.CASHIER.PLAYER.PM')}}</div>
          </td><td>
          <div>{{comment.limits ? comment.limits.maxAmountDay : ''}}</div>
          <div>{{comment.limits ? comment.limits.maxAmountWeek : ''}}</div>
          <div>{{comment.limits ? comment.limits.maxAmountMonth : ''}}</div>
        </td><td>
          <div>{{comment.limits ? comment.limits.maxTransactionsDay : ''}}</div>
          <div>{{comment.limits ? comment.limits.maxTransactionsWeek : ''}}</div>
          <div>{{comment.limits ? comment.limits.maxTransactionsMonth : ''}}</div>
        </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script lang="ts">
import {Component, Prop, Mixins} from "vue-property-decorator";
import {CashierConfigComment} from "@/core/interface/cashierConfig/CashierConfigInterface";
import TranslationMixin from "@/core/mixins/translationMixin";

@Component
export default class CashierConfigCommentComponent  extends Mixins(TranslationMixin) {
  @Prop() comment!: CashierConfigComment
}

</script>

<style scoped>

</style>