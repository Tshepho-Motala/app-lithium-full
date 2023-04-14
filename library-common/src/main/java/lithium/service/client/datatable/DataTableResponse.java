package lithium.service.client.datatable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Data;

@Data
public class DataTableResponse<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private ArrayList<T> data = new ArrayList<T>();
	private String draw;
	private long recordsTotal;
	private long recordsFiltered;

    private int recordsTotalPages;

    private long currentPage;
	
	public DataTableResponse() {
	}

    public DataTableResponse(DataTableRequest request, Page<T> list, Page<T> filteredList) {
        this(request, filteredList, list.getTotalElements(), filteredList.getTotalElements());
    }

	public DataTableResponse(DataTableRequest request, Page<T> list) {
		this(request, list, list.getTotalElements(), list.getTotalElements());
	}

	public DataTableResponse(DataTableRequest request, Page<T> list, long recordsTotal, long recordsFiltered) {
		for (T object: list) {
			this.data.add(object);
		}
		this.draw = request.getEcho();
		this.recordsTotal = recordsTotal;
		this.recordsFiltered = recordsFiltered;
	}

	public DataTableResponse(DataTableRequest request, Page<T> list, long recordsTotal, long currentPage, int recordsTotalPages) {
		for (T object: list) {
			this.data.add(object);
		}
		this.draw = request.getEcho();
		this.recordsTotal = recordsTotal;
		this.currentPage = currentPage;
		this.recordsTotalPages = recordsTotalPages;
	}

	public DataTableResponse(DataTableRequest request, List<T> list) {
		this(request, list, list.size(), list.size());
	}

	public DataTableResponse(DataTableRequest request, List<T> list, long recordsTotal, long recordsFiltered) {
		for (T object: list) {
			this.data.add(object);
		}
		this.draw = request.getEcho();
		this.recordsTotal = recordsTotal;
		this.recordsFiltered = recordsFiltered;
	}

}